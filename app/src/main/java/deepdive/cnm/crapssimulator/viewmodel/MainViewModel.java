package deepdive.cnm.crapssimulator.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.craps.model.Game;
import edu.cnm.deepdive.craps.model.Game.Round;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainViewModel extends AndroidViewModel {

  private final Random rng;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Round> round;
  private final MutableLiveData<Boolean> running;
  private final long baseTime;
  private Runner runner;
  private long timeStep;
  private ScheduledExecutorService es =
      Executors.newSingleThreadScheduledExecutor();

  public MainViewModel(@NonNull Application application) {
    super(application);
    rng = new SecureRandom();
    game = new MutableLiveData<>();
    round = new MutableLiveData<>();
    running = new MutableLiveData<>();
    baseTime = 1000000;
    timeStep = baseTime;
    runner = new Runner();
    reset();
  }

  public LiveData<Game> getGame() {
    return game;
  }

  public LiveData<Round> getRound() {
    return round;
  }

  public LiveData<Boolean> isRunning() {
    return running;
  }

  public void playOne() {
    round.setValue(game.getValue().play());
    game.setValue(game.getValue());
  }

  public void slowDown(){
    running.setValue(true);
    timeStep *= 2;
    es.shutdown();
    es = Executors.newSingleThreadScheduledExecutor();
    es.scheduleAtFixedRate(runner,0,timeStep,TimeUnit.MICROSECONDS);

  }
  public void fastForward() {
    running.setValue(true);
    es.shutdown();
    es = Executors.newSingleThreadScheduledExecutor();
    if(timeStep>1){
      es.scheduleAtFixedRate(runner,0, timeStep, TimeUnit.MICROSECONDS);
      timeStep /= (timeStep / 2 > 1) ? 2 : 1;

    }
  }

  public void pause() {
    timeStep = baseTime;
    running.setValue(false);
    es.shutdown();
  }

  public void reset() {
    game.setValue(new Game(rng));
    round.setValue(null);
    running.setValue(false);
  }

  private class Runner implements Runnable {


    @Override
    public void run() {
      Game game = MainViewModel.this.game.getValue();
      MainViewModel.this.round.postValue(game.play());
      MainViewModel.this.game.postValue(game);
    }


  }

}
