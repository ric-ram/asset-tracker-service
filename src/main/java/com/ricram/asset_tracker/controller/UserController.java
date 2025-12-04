package com.ricram.asset_tracker.controller;

import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;
import com.ricram.asset_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<CreateUserRespDto> createUser(@Valid @RequestBody CreateUserReqDto req) {
        CreateUserRespDto dto = userService.createUser(req);
        URI location = URI.create("/users/" + dto.id());

        return ResponseEntity
                .created(location)
                .body(dto);
    }

    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }
}
