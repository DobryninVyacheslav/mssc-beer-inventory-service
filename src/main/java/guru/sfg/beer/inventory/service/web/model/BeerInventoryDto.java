package guru.sfg.beer.inventory.service.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by jt on 2019-05-31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerInventoryDto implements Serializable {

    private static final long serialVersionUID = 5197963334649684262L;

    private UUID id;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;
    private UUID beerId;
    private Integer quantityOnHand;
}
