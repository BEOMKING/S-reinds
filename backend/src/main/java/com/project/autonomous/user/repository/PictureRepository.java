package com.project.autonomous.user.repository;

import com.project.autonomous.common.entity.Picture;
import com.project.autonomous.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Picture, String> {

}
