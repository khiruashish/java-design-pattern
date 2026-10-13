package com.handbook.architecture.hexagonal.checkout.adapter.in;

import com.handbook.architecture.hexagonal.checkout.port.in.PlaceOrderUseCase;
import com.handbook.architecture.hexagonal.checkout.port.in.PlaceOrderUseCase.PlaceOrderCommand;

/** Another driving adapter: support staff place phone orders from a terminal. Same use case. */
public class CliCheckoutAdapter {

    private final PlaceOrderUseCase useCase;

    public CliCheckoutAdapter(PlaceOrderUseCase useCase) {
        this.useCase = useCase;
    }

    /** Input like: {@code order CUST-77 +91-98450-22222 64900} (amount in paise). */
    public String run(String commandLine) {
        String[] p = commandLine.split(" ");
        return "cli> " + useCase.placeOrder(new PlaceOrderCommand(p[1], p[2], Long.parseLong(p[3]))).status();
    }
}
