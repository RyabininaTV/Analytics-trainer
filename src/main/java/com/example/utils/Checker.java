package com.example.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class Checker {

    public CheckChain begin() {
        return new CheckChain();
    }

    public static final class CheckChain {
        private boolean currentCondition;

        public CheckChain when(boolean condition) {
            this.currentCondition = condition;
            return this;
        }

        public CheckChain thenThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
            if (currentCondition) {
                throw exceptionSupplier.get();
            }
            return this;
        }
    }
}
