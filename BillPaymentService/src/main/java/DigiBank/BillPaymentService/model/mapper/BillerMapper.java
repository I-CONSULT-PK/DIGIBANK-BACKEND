package DigiBank.BillPaymentService.model.mapper;

import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.model.entity.Biller;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillerMapper {
    Biller dtoToEntity(BillerDtoRequest dto);
    BillerDtoRequest entityToDto(Biller  entity);
}
