package com.woundex.user;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.woundex.user.Repositories.DriverRepository;
import com.woundex.user.Repositories.RiderRepository;
import com.woundex.user.Services.UserService;
import com.woundex.user.dto.LoginRequest;
import com.woundex.user.dto.SignUpRequest;
import com.woundex.user.entities.DriverEntity;
import com.woundex.user.entities.RiderEntity;
import com.woundex.user.dto.LoginResponse;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RiderRepository riderRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerDriver_savesDriverWithHashedPassword() {
        SignUpRequest req = mock(SignUpRequest.class);
        when(req.getRole()).thenReturn("DRIVER");
        when(req.getName()).thenReturn("John Doe");
        when(req.getEmail()).thenReturn("john@example.com");
        when(req.getPhone()).thenReturn("1234567890");
        when(req.getPassword()).thenReturn("secret");
        when(req.getLicence_number()).thenReturn("LIC123");
        when(req.getVehicle_info()).thenReturn("Toyota");

        userService.registerDriver(req);

        ArgumentCaptor<DriverEntity> captor = ArgumentCaptor.forClass(DriverEntity.class);
        verify(driverRepository).save(captor.capture());
        DriverEntity saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
        assertThat(saved.getPhone()).isEqualTo("1234567890");
        assertThat(saved.getLicenceNumber()).isEqualTo("LIC123");
        assertThat(saved.getVehicleInfo()).isEqualTo("Toyota");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertThat(encoder.matches("secret", saved.getPasswordHash())).isTrue();
    }

    @Test
    void loginUser_driver_successAndInvalidPassword() {
        // prepare stored driver with hashed password
        String raw = "secret";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(raw);

        DriverEntity driver = DriverEntity.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .phone("123")
                .passwordHash(hashed)
                .build();

        when(driverRepository.findByEmail("john@example.com")).thenReturn(Optional.of(driver));

        LoginRequest loginReq = mock(LoginRequest.class);
        when(loginReq.getRole()).thenReturn("DRIVER");
        when(loginReq.getEmail()).thenReturn("john@example.com");
        when(loginReq.getPassword()).thenReturn(raw);

        LoginResponse resp = userService.loginUser(loginReq);
        assertThat(resp).isNotNull();
        assertThat(resp.getUserId()).isEqualTo(driver.getId().toString());
        assertThat(resp.getName()).isEqualTo(driver.getName());
        assertThat(resp.getEmail()).isEqualTo(driver.getEmail());
        assertThat(resp.getRole()).isEqualTo("DRIVER");
        assertThat(resp.getAccessToken()).isNull();
        assertThat(resp.getExpiresIn()).isNull();

        // invalid password case
        LoginRequest badReq = mock(LoginRequest.class);
        when(badReq.getRole()).thenReturn("DRIVER");
        when(badReq.getEmail()).thenReturn("john@example.com");
        when(badReq.getPassword()).thenReturn("wrong");

        assertThatThrownBy(() -> userService.loginUser(badReq))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid password");
    }

    @Test
    void loginUser_rider_success() {
        String raw = "rsecret";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(raw);

        RiderEntity rider = RiderEntity.builder()
                .id(UUID.randomUUID())
                .name("Rita Rider")
                .email("rita@example.com")
                .phone("321")
                .passwordHash(hashed)
                .build();

        when(riderRepository.findByEmail("rita@example.com")).thenReturn(Optional.of(rider));

        LoginRequest req = mock(LoginRequest.class);
        when(req.getRole()).thenReturn("RIDER");
        when(req.getEmail()).thenReturn("rita@example.com");
        when(req.getPassword()).thenReturn(raw);

        LoginResponse resp = userService.loginUser(req);
        assertThat(resp.getUserId()).isEqualTo(rider.getId().toString());
        assertThat(resp.getName()).isEqualTo(rider.getName());
        assertThat(resp.getEmail()).isEqualTo(rider.getEmail());
        assertThat(resp.getRole()).isEqualTo("RIDER");
    }
}