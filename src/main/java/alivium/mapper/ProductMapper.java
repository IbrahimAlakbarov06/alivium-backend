package alivium.mapper;

import alivium.domain.entity.Product;
import alivium.model.dto.request.ProductCreateRequest;
import alivium.model.dto.request.ProductUpdateRequest;
import alivium.model.dto.response.ProductResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductCreateRequest request);

    ProductResponse toResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductUpdateRequest request, @MappingTarget Product product);
}
