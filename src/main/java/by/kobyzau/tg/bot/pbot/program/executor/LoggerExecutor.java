package by.kobyzau.tg.bot.pbot.program.executor;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;

import java.util.concurrent.Executor;


public class LoggerExecutor implements Executor {

    private final Executor executor;
    private final Logger logger;

    public LoggerExecutor(Executor executor, Logger logger) {
        this.executor = executor;
        this.logger = logger;
    }

    @Override
    public void execute(Runnable runnable) {
        executor.execute(() -> {
            try{
                runnable.run();
            }catch (Exception e) {
                logger.error("Error in parallel thread", e);
            }
        });
    }
}
