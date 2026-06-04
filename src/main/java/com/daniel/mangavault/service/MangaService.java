package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.MangaCreationRequest;
import com.daniel.mangavault.dto.response.MangaResponse;
import com.daniel.mangavault.entity.Manga;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.repository.MangaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;

    public MangaResponse createManga(MangaCreationRequest request){
        Manga manga = Manga.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .description(request.getDescription())
                .coverUrl(request.getCoverUrl())
                .status(request.getStatus())
                .visibility(request.getVisibility())
                .build();

        Manga savedManga = mangaRepository.save(manga);

        return MangaResponse.builder()
                .id(savedManga.getId())
                .title(savedManga.getTitle())
                .slug(savedManga.getSlug())
                .description(savedManga.getDescription())
                .coverUrl(savedManga.getCoverUrl())
                .status(savedManga.getStatus())
                .visibility(savedManga.getVisibility())
                .createdAt(savedManga.getCreatedAt())
                .updatedAt(savedManga.getUpdatedAt())
                .build();
    }

    public List<MangaResponse> getAllMangas() {
        return mangaRepository.findAll()
                .stream()
                .map(manga -> MangaResponse.builder()
                        .id(manga.getId())
                        .title(manga.getTitle())
                        .slug(manga.getSlug())
                        .description(manga.getDescription())
                        .coverUrl(manga.getCoverUrl())
                        .status(manga.getStatus())
                        .visibility(manga.getVisibility())
                        .createdAt(manga.getCreatedAt())
                        .updatedAt(manga.getUpdatedAt())
                        .build())
                .toList();
    }

    public MangaResponse getMangaById(String id){
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new AppException("Manga not found"));

        return MangaResponse.builder()
                .id(manga.getId())
                .title(manga.getTitle())
                .slug(manga.getSlug())
                .description(manga.getDescription())
                .coverUrl(manga.getCoverUrl())
                .status(manga.getStatus())
                .visibility(manga.getVisibility())
                .createdAt(manga.getCreatedAt())
                .updatedAt(manga.getUpdatedAt())
                .build();

    }
}
