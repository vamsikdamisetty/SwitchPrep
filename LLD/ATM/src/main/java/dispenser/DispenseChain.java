package dispenser;

public interface DispenseChain {
        void setNextChain(DispenseChain nextChain);

        boolean canDispense(int amount);

        void dispense(int amount);
}
