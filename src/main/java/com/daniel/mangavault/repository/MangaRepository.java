package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Manga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MangaRepository extends JpaRepository<Manga, String> {
}