package com.handbook.architecture.clean.coupon.usecase;

import com.handbook.architecture.clean.coupon.entities.Coupon;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Use-case circle: the boundaries (input/output ports), request/response models, the gateways the
 * use case needs, and the interactor. It depends only on entities.
 */
public final class ApplyCoupon {

    private ApplyCoupon() {
    }

    public record Request(String cartId, String couponCode) { // (1)!
    }

    public record Response(boolean applied, long subtotalPaise, long discountPaise, String message) {
    }

    public interface InputBoundary {
        void apply(Request request, OutputBoundary presenter);
    }

    public interface OutputBoundary { // (2)!
        void present(Response response);
    }

    public interface CartGateway {
        Optional<Long> subtotalOf(String cartId);
    }

    public interface CouponGateway {
        Optional<Coupon> find(String code);
    }

    public interface Calendar {
        LocalDate today();
    }

    public static final class Interactor implements InputBoundary {
        private final CartGateway carts;
        private final CouponGateway coupons;
        private final Calendar calendar;

        public Interactor(CartGateway carts, CouponGateway coupons, Calendar calendar) {
            this.carts = carts;
            this.coupons = coupons;
            this.calendar = calendar;
        }

        @Override
        public void apply(Request request, OutputBoundary presenter) {
            long subtotal = carts.subtotalOf(request.cartId()).orElse(0L);
            Optional<Coupon> coupon = coupons.find(request.couponCode().trim().toUpperCase());
            if (coupon.isEmpty()) {
                presenter.present(new Response(false, subtotal, 0, "unknown coupon " + request.couponCode()));
                return;
            }
            switch (coupon.get().evaluate(subtotal, calendar.today())) { // (3)!
                case Coupon.Discount d -> presenter.present(new Response(true, subtotal, d.paise(), "coupon applied"));
                case Coupon.NotApplicable n -> presenter.present(new Response(false, subtotal, 0, n.reason()));
            }
        }
    }
}
