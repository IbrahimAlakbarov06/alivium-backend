package alivium.controller;

import alivium.model.dto.request.CollectionRequest;
import alivium.model.dto.response.CollectionResponse;
import alivium.model.enums.CollectionType;
import alivium.service.CollectionService;
import alivium.service.impl.CollectionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
@Validated
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping("/active")
    public ResponseEntity<List<CollectionResponse>> getActiveCollections() {
        return ResponseEntity.ok(collectionService.getActiveCollections());
    }

    @GetMapping("/current")
    public ResponseEntity<List<CollectionResponse>> getCurrentActiveCollections() {
        return ResponseEntity.ok(collectionService.getCurrentActiveCollections());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CollectionResponse>> getCollectionsByType(@PathVariable CollectionType type) {
        return ResponseEntity.ok(collectionService.getCollectionsByType(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionResponse> getCollectionById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.getCollectionById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CollectionResponse> getCollectionByName(@PathVariable String name) {
        return ResponseEntity.ok(collectionService.getCollectionByName(name));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<CollectionResponse>> getAllCollections() {
        return ResponseEntity.ok(collectionService.getAllCollections());
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<CollectionResponse> createCollection(@Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectionService.createCollection(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<CollectionResponse> updateCollection(
            @PathVariable Long id,
            @Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.ok(collectionService.updateCollection(id, request));
    }

    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<CollectionResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.toggleCollection(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{collectionId}/products/{productId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<CollectionResponse> addProductToCollection(
            @PathVariable Long collectionId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(collectionService.addProductToCollection(collectionId, productId));
    }

    @DeleteMapping("/{collectionId}/products/{productId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<CollectionResponse> removeProductFromCollection(
            @PathVariable Long collectionId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(collectionService.removeProductFromCollection(collectionId, productId));
    }
}