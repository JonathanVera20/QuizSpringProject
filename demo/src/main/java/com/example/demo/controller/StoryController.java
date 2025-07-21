package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Story;
import com.example.demo.service.StoryService;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    // GET: Obtiene todas las historias
    @GetMapping
    public List<Story> getAllStories() {
        return storyService.getAllStories();
    }

    // GET: Obtiene una historia por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Story> getStoryById(@PathVariable Long id) {
        Story story = storyService.getStoryById(id);
        if (story != null) {
            return ResponseEntity.ok(story);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST: Crea una nueva historia (solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Story createStory(@RequestBody Story story) {
        return storyService.createStory(story);
    }

    // PUT: Actualiza una historia por su ID (solo ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Story> updateStory(@PathVariable Long id, @RequestBody Story newStory) {
        Story updatedStory = storyService.updateStory(newStory);
        if (updatedStory != null) {
            return ResponseEntity.ok(updatedStory);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // DELETE: Elimina una historia por su ID (solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        storyService.deleteStory(id);
        return ResponseEntity.noContent().build();
    }

}
