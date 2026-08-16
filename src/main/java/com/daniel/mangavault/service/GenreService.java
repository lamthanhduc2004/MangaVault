package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.GenreRequest;
import com.daniel.mangavault.dto.response.GenreResponse;
import com.daniel.mangavault.entity.Genre;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/** Genre catalogue (F18). */
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<GenreResponse> getGenres() {
        return genreRepository.findAllByOrderByNameAsc().stream()
                .map(GenreService::mapToResponse)
                .toList();
    }

    public GenreResponse createGenre(GenreRequest request) {
        if (genreRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.GENRE_NAME_ALREADY_EXISTS);
        }
        if (genreRepository.existsBySlug(request.getSlug())) {
            throw new AppException(ErrorCode.GENRE_SLUG_ALREADY_EXISTS);
        }

        Genre genre = genreRepository.save(Genre.builder()
                .name(request.getName().trim())
                .slug(request.getSlug().trim())
                .description(normalizeDescription(request.getDescription()))
                .build());

        return mapToResponse(genre);
    }

    public GenreResponse updateGenre(String id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));

        // Exclude the row being edited, otherwise saving without changes reports a clash.
        if (genreRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new AppException(ErrorCode.GENRE_NAME_ALREADY_EXISTS);
        }
        if (genreRepository.existsBySlugAndIdNot(request.getSlug(), id)) {
            throw new AppException(ErrorCode.GENRE_SLUG_ALREADY_EXISTS);
        }

        genre.setName(request.getName().trim());
        genre.setSlug(request.getSlug().trim());
        genre.setDescription(normalizeDescription(request.getDescription()));

        return mapToResponse(genreRepository.save(genre));
    }

    public void deleteGenre(String id) {
        if (!genreRepository.existsById(id)) {
            throw new AppException(ErrorCode.GENRE_NOT_FOUND);
        }
        // Requirement: only a genre that no story uses may be removed.
        if (genreRepository.countStoriesUsingGenre(id) > 0) {
            throw new AppException(ErrorCode.GENRE_IN_USE);
        }
        genreRepository.deleteById(id);
    }

    static GenreResponse mapToResponse(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .slug(genre.getSlug())
                .description(genre.getDescription())
                .build();
    }

    private static String normalizeDescription(String description) {
        return StringUtils.hasText(description) ? description.trim() : null;
    }
}
