package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Métodos CRUD básicos proporcionados por JpaRepository
    // save, findById, delete, etc.

    // Encuentra un usuario por su nombre de usuario
    User findByUsername(String username);

    // Encuentra un usuario por su correo electrónico
    User findByEmail(String email);

}
