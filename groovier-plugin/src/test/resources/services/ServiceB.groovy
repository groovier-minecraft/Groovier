package services

class ServiceB {

    private final serviceA = new ServiceA()

    void greeting() {
        serviceA.sayHello()
    }

    void farewell() {
       serviceA.sayGoodbye()
    }
}
