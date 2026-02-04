
package moe.nea.notfimament.init;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        new HandledScreenRiser().addTinkerers();
        new SectionBuilderRiser().addTinkerers();
    }
}
