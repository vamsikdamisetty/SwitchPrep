package dispenser;

public abstract class NotesDispenser implements DispenseChain{

    private DispenseChain nextChain;
    private final int noteValue;
    private int numNotes;

    public NotesDispenser(int noteValue, int numNotes){
        this.noteValue = noteValue;
        this.numNotes = numNotes;
    }

    @Override
    public void setNextChain(DispenseChain nextChain) {
        this.nextChain = nextChain;
    }

    @Override
    public boolean canDispense(int amount) {
        if (amount < 0) return false;
        if (amount == 0) return true;

        int notesToUse = Math.min(amount/noteValue,this.numNotes);
        int remainingAmount = amount - notesToUse *this.noteValue;


        if(remainingAmount == 0) return  true;

        if(this.nextChain != null){
            return this.nextChain.canDispense(remainingAmount);
        }
        return false;
    }

    @Override
    public void dispense(int amount) {
        if(amount >= noteValue){
            int notesToDispense = Math.min(amount/noteValue,this.numNotes);
            int remainingAmount = amount - notesToDispense *this.noteValue;

            if (notesToDispense > 0) {
                System.out.println("Dispensing " + notesToDispense + " x $" + noteValue + " note(s)");
                this.numNotes -= notesToDispense;
            }

            if(remainingAmount == 0) return;

            if(this.nextChain != null){
               this.nextChain.dispense(remainingAmount);
            }

        }else{
            this.nextChain.dispense(amount);
        }
    }
}
