package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest request) {
        AllocateOrderResult allocateOrderResult = new AllocateOrderResult();
        allocateOrderResult.setBeerOrderDto(request.getBeerOrderDto());

        try {
            boolean isAllocated = allocationService.allocateOrder(request.getBeerOrderDto());
            allocateOrderResult.setPendingInventory(!isAllocated);
            allocateOrderResult.setAllocationError(false);
        } catch (Exception ex) {
            log.error("Allocation failed for Order Id: " + request.getBeerOrderDto().getId());
            allocateOrderResult.setAllocationError(true);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, allocateOrderResult);

    }

}
