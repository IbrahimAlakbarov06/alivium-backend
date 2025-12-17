package alivium.service;

import alivium.model.dto.request.CollectionRequest;
import alivium.model.dto.response.CollectionResponse;
import alivium.model.dto.response.MessageResponse;
import alivium.model.enums.CollectionType;

import java.util.List;

public interface CollectionService {

    CollectionResponse createCollection(CollectionRequest request);

    CollectionResponse updateCollection(Long collectionId, CollectionRequest request);

    List<CollectionResponse> getAllCollections();

    List<CollectionResponse> getActiveCollections();

    List<CollectionResponse> getCollectionsByType(CollectionType type);

    List<CollectionResponse> getCurrentActiveCollections();

    CollectionResponse getCollectionById(Long id);

    CollectionResponse getCollectionByName(String name);

    MessageResponse deleteCollection(Long id);

    CollectionResponse toggleCollection(Long id);

    CollectionResponse addProductToCollection(Long collectionId, Long productId);

    CollectionResponse removeProductFromCollection(Long collectionId, Long productId);
}
