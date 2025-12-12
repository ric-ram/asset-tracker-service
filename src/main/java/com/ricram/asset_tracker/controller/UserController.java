package com.ricram.asset_tracker.controller;

import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;
import com.ricram.asset_tracker.dto.UserInfoDto;
import com.ricram.asset_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<CreateUserRespDto> createUser(@Valid @RequestBody CreateUserReqDto req) {
        CreateUserRespDto resp = userService.createUser(req);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{portfolioId}")
                .buildAndExpand(resp.id())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(resp);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable UUID userId) {
        UserInfoDto resp = userService.getUserInfo(userId);

        return ResponseEntity
                .ok()
                .body(resp);
    }
}
