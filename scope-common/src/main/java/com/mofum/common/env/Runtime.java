package com.mofum.common.env;

/**
 * 运行时
 */
final class Runtime {

    static ThreadLocal<ThreadEnv> THREAD_ENV = new InheritableThreadLocal<>();

    static {
        THREAD_ENV.set(new ThreadEnv());
    }

    /**
     * 无法构造实例
     */
    private Runtime() {
        throw new RuntimeException("This is static class.");
    }

}