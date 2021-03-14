package guru.sfg.beer.inventory.service.services;

import guru.sfg.brewery.model.BeerOrderDto;

public interface AllocationService {

    boolean allocateOrder(BeerOrderDto dto);

    void deallocateOrder(BeerOrderDto beerOrderDto);
}
