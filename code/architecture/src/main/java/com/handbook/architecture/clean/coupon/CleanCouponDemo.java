package com.handbook.architecture.clean.coupon;

import com.handbook.architecture.clean.coupon.adapters.CouponAdapters;
import com.handbook.architecture.clean.coupon.adapters.CouponAdapters.CouponController;
import com.handbook.architecture.clean.coupon.adapters.CouponAdapters.MobileToastPresenter;
import com.handbook.architecture.clean.coupon.adapters.CouponAdapters.WebPresenter;
import com.handbook.architecture.clean.coupon.entities.Coupon;
import com.handbook.architecture.clean.coupon.usecase.ApplyCoupon;

import java.time.LocalDate;
import java.util.Map;

public class CleanCouponDemo {

    public static void main(String[] args) {
        // "Main" component: the only place that knows every concrete class.
        var useCase = new ApplyCoupon.Interactor(
                CouponAdapters.carts(Map.of("CART-1", 144_700L, "CART-2", 39_900L)),
                CouponAdapters.coupons(Map.of(
                        "MANGO20", new Coupon("MANGO20", 20, 50_000, 20_000, LocalDate.of(2026, 10, 31)),
                        "DIWALI10", new Coupon("DIWALI10", 10, 0, 50_000, LocalDate.of(2025, 11, 5)))),
                CouponAdapters.fixedCalendar(LocalDate.of(2026, 10, 14)));
        var controller = new CouponController(useCase);

        System.out.println("=== Grocery: one use case, two presenters ===");
        WebPresenter web = new WebPresenter();
        controller.post(Map.of("cart", "CART-1", "code", "mango20"), web);
        System.out.println("web    CART-1 MANGO20  -> " + web.viewModel);

        MobileToastPresenter mobile = new MobileToastPresenter();
        controller.post(Map.of("cart", "CART-1", "code", "MANGO20"), mobile);
        System.out.println("mobile CART-1 MANGO20  -> " + mobile.viewModel);
        controller.post(Map.of("cart", "CART-2", "code", "MANGO20"), mobile);
        System.out.println("mobile CART-2 MANGO20  -> " + mobile.viewModel);
        controller.post(Map.of("cart", "CART-1", "code", "DIWALI10"), mobile);
        System.out.println("mobile CART-1 DIWALI10 -> " + mobile.viewModel);
    }
}
