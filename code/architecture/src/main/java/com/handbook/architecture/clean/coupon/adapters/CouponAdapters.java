package com.handbook.architecture.clean.coupon.adapters;

import com.handbook.architecture.clean.coupon.entities.Coupon;
import com.handbook.architecture.clean.coupon.usecase.ApplyCoupon;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Interface-adapters circle: controllers turn web/app input into request models, presenters turn
 * response models into view models, gateways implement the use case's data interfaces.
 */
public final class CouponAdapters {

    private CouponAdapters() {
    }

    /** Controller: knows the HTTP shape of the input, nothing about rules. */
    public static final class CouponController {
        private final ApplyCoupon.InputBoundary useCase;

        public CouponController(ApplyCoupon.InputBoundary useCase) {
            this.useCase = useCase;
        }

        public void post(Map<String, String> form, ApplyCoupon.OutputBoundary presenter) {
            useCase.apply(new ApplyCoupon.Request(form.get("cart"), form.getOrDefault("code", "")), presenter);
        }
    }

    /** Web presenter: a detailed view model with rupee strings for the desktop basket page. */
    public static final class WebPresenter implements ApplyCoupon.OutputBoundary {
        public String viewModel;

        @Override
        public void present(ApplyCoupon.Response r) { // (1)!
            viewModel = String.format("{\"ok\":%s,\"subtotal\":\"₹%,.2f\",\"discount\":\"-₹%,.2f\",\"total\":\"₹%,.2f\",\"note\":\"%s\"}",
                    r.applied(), r.subtotalPaise() / 100.0, r.discountPaise() / 100.0,
                    (r.subtotalPaise() - r.discountPaise()) / 100.0, r.message());
        }
    }

    /** Mobile presenter: one short line for a toast message. */
    public static final class MobileToastPresenter implements ApplyCoupon.OutputBoundary {
        public String viewModel;

        @Override
        public void present(ApplyCoupon.Response r) {
            viewModel = r.applied() ? "🎉 You saved ₹" + r.discountPaise() / 100 : "⚠ " + r.message();
        }
    }

    /** Gateways: in-memory here; JPA, Redis or a remote API in production. */
    public static ApplyCoupon.CartGateway carts(Map<String, Long> subtotals) {
        return id -> Optional.ofNullable(subtotals.get(id));
    }

    public static ApplyCoupon.CouponGateway coupons(Map<String, Coupon> byCode) {
        return code -> Optional.ofNullable(byCode.get(code));
    }

    public static ApplyCoupon.Calendar fixedCalendar(LocalDate date) {
        return () -> date;
    }
}
