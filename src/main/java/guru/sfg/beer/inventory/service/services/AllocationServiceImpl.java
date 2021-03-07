package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final BeerInventoryRepository beerInventoryRepository;

    @Override
    public boolean allocateOrder(BeerOrderDto dto) {
        log.debug("Allocating OrderId: " + dto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        dto.getBeerOrderLines().forEach(beerOrderLineDto -> {

            if (safeSubtraction(beerOrderLineDto.getOrderQuantity(), beerOrderLineDto.getQuantityAllocated()) > 0) {
                allocateBeerOrderLine(beerOrderLineDto);
            }
            totalOrdered.set(totalOrdered.get() + safeIntValue(beerOrderLineDto.getOrderQuantity()));
            totalAllocated.set(totalAllocated.get() + safeIntValue(beerOrderLineDto.getQuantityAllocated()));
        });

        log.debug("Total Ordered: " + totalOrdered.get() + " | Total Allocated: " + totalAllocated.get());

        return totalOrdered.get() == totalAllocated.get();
    }

    private int safeSubtraction(Integer orderQuantity, Integer quantityAllocated) {
        return safeIntValue(orderQuantity) - safeIntValue(quantityAllocated);
    }

    private int safeIntValue(Integer value) {
        return value == null ? 0 : value;
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLineDto) {
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(beerOrderLineDto.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            int inventory = safeIntValue(beerInventory.getQuantityOnHand());
            int orderQty = safeIntValue(beerOrderLineDto.getOrderQuantity());
            int allocatedQty = safeIntValue(beerOrderLineDto.getQuantityAllocated());
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) {
                inventory -= qtyToAllocate;
                beerOrderLineDto.setQuantityAllocated(orderQty);
                beerInventory.setQuantityOnHand(inventory);
                beerInventoryRepository.save(beerInventory);
            } else if (inventory > 0) {
                beerOrderLineDto.setQuantityAllocated(allocatedQty + inventory);
                beerInventory.setQuantityOnHand(0);
                beerInventoryRepository.delete(beerInventory);
            }
        });
    }
}
