package com.example.Uniride.Service;

import com.example.Uniride.DTO.LoginDTO;
import com.example.Uniride.DTO.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginDTO dto);
}